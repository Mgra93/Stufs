using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public interface IUser
    {
        Task<User> Login(string username, string password);
        Task<int> CreateUser(RegistrationDTO user);
        Task<bool> ChangePassword(ChangePasswordDTO dto);
        Task<List<UserDTO>> GetAllUsers();
        Task<UserDTO> GetUserByUsername(string username);
        Task<User> FindUser(string username);
        Task<bool> UpdateUser(UserDTO dto);
        Task<List<UserDTO>> GetUserByFilter(UserFilter filter);
    }
}
