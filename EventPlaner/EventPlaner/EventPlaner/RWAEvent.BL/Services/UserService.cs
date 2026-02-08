using AutoMapper;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Security;

namespace RWAEvent.BL.Services
{
    public class UserService : IUser
    {
        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;

        public UserService(RwaeventContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }


        public async Task<List<UserDTO>> GetUserByFilter(UserFilter filter)
        {
            var query = _context.Users
                .Where(u => u.Active)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(u =>
                    u.Username.Contains(filter.Search) ||
                    u.FirstName.Contains(filter.Search) ||
                    u.LastName.Contains(filter.Search) ||
                    u.Email.Contains(filter.Search));
            }

            int skip = (filter.Page - 1) * filter.PageSize;

            var users = await query
                .OrderByDescending(u => u.CreatedOn)
                .Skip(skip)
                .Take(filter.PageSize)
                .ToListAsync();

            return _mapper.Map<List<UserDTO>>(users);
        }

        public async Task<bool> UpdateUser(UserDTO dto)
        {
            var existingUser = await _context.Users.FindAsync(dto.Id);
            if (existingUser == null)
                return false;

            if (dto.ChangePassword)
            {
                var oldHash = PasswordHashProvider.GetHash(dto.OldPassword, existingUser.PwdSalt);
                if (oldHash != existingUser.PwdHash)
                    throw new InvalidOperationException("Stara lozinka je pogrešna");

                var newSalt = PasswordHashProvider.GetSalt();
                var newHash = PasswordHashProvider.GetHash(dto.NewPassword, newSalt);

                existingUser.PwdSalt = newSalt;
                existingUser.PwdHash = newHash;
            }

            existingUser.Username = dto.Username;
            existingUser.FirstName = dto.FirstName;
            existingUser.LastName = dto.LastName;
            existingUser.Email = dto.Email;
            existingUser.Phone = dto.Phone;
            existingUser.Role = dto.Role;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<List<UserDTO>> GetAllUsers()
        {
            var users = await _context.Users.ToListAsync();
            return _mapper.Map<List<UserDTO>>(users);
        }

        public async Task<UserDTO> GetUserByUsername(string username)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username && u.Active);

            if (user == null)
            {
                return null;
            }

            return _mapper.Map<UserDTO>(user);
        }

        public async Task<int> CreateUser(RegistrationDTO dto)
        {
            var salt = PasswordHashProvider.GetSalt();
            var hash = PasswordHashProvider.GetHash(dto.Password, salt);

            var user = _mapper.Map<User>(dto);

            user.PwdSalt = salt;
            user.PwdHash = hash;
            user.Role = (int)Role.User;
            user.Active = true;
            user.CreatedOn = DateTime.Now;

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            return user.Id;
        }

        public async Task<bool> ChangePassword(ChangePasswordDTO dto)
        {
            var user = await FindUser(dto.Username);

            if (user == null)
            {
                throw new InvalidOperationException("Korisnik ne posoji");
            }

            var currentHash = PasswordHashProvider.GetHash(dto.CurrentPassword, user.PwdSalt);
            if (currentHash != user.PwdHash)
            {
                throw new InvalidOperationException("Pogrešna trenutna lozinka");
            }

            if (dto.NewPassword != dto.ConfirmPassword)
            {
                throw new InvalidOperationException("Lozinka i ponovljena lozinka se ne podudaraju");
            }

            var newSalt = PasswordHashProvider.GetSalt();
            var newHash = PasswordHashProvider.GetHash(dto.NewPassword, newSalt);

            user.PwdSalt = newSalt;
            user.PwdHash = newHash;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<User> Login(string username, string password)
        {
            var user = await _context.Users
                .FirstOrDefaultAsync(u => u.Username == username);

            if (user == null)
                throw new InvalidOperationException("Korisnik ne postoji");

            var hash = PasswordHashProvider.GetHash(password, user.PwdSalt);
            if (hash != user.PwdHash)
                throw new InvalidOperationException("Pogrešna lozinka");

            return user;
        }

        public async Task<User> FindUser(string username)
        {
            var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == username && u.Active);

            return user;
        }
    }
}
