using AutoMapper;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Reservation;
using RWAEvent.BL.Models.Enums;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public class ReservationService : IReservation
    {

        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;
        private readonly IUser _userService;

        public ReservationService(RwaeventContext context, IMapper mapper, IUser userService)
        {
            _context = context;
            _mapper = mapper;
            _userService = userService;
        }

        public async Task<int> CreateReservations(List<ReservationDTO> reservationList, string userName)
        {
            if (reservationList == null || !reservationList.Any())
                return 0;

            var user = await _userService.GetUserByUsername(userName);
            if (user == null)
                return 0;

            foreach (var dto in reservationList)
            {
                var existingEvent = await _context.Events
                    .FirstOrDefaultAsync(e => e.Id == dto.EventId);

                if (existingEvent != null)
                {
                    var reservation = new Reservation
                    {
                        EventId = (int)dto.EventId,
                        TicketNumber = dto.TicketNumber,
                        UserId = user.Id,
                        Status = dto.Status,
                        TotalPrice = existingEvent.Price * dto.TicketNumber,
                        CreatedOn = DateTime.Now,
                        Active = true
                    };

                    _context.Reservations.Add(reservation);
                }
            }

            return await _context.SaveChangesAsync();
        }

        public async Task<List<ReservationDTO>> GetReservationsByFilter(ReservationFilter filter)
        {
            var query = _context.Reservations
                .Include(r => r.User)
                .Include(r => r.Event)
                    .ThenInclude(e => e.EventPerformers)
                        .ThenInclude(ep => ep.Performer)
                .Include(r => r.Event)
                    .ThenInclude(e => e.EventType)
                    .Where(r => r.Active)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(filter.User))
            {
                var user = await _context.Users.FirstOrDefaultAsync(u => u.Username == filter.User);

                if (user != null && user.Role != 1)
                {
                    query = query.Where(r => r.User.Username == filter.User);
                }
            }

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                query = query.Where(r =>
                    r.Event.Title.Contains(filter.Search) ||
                    r.Event.Location.Contains(filter.Search) ||
                    r.Event.EventType.Name.Contains(filter.Search) ||
                    r.User.Username.Contains(filter.Search)
                );
            }

            int skip = (filter.Page - 1) * filter.PageSize;
            query = query.OrderByDescending(r => r.CreatedOn).Skip(skip).Take(filter.PageSize);

            var reservationList = await query.ToListAsync();
            var dtoList = _mapper.Map<List<ReservationDTO>>(reservationList);

            return dtoList;
        }

        public async Task<bool> UpdateReservation(ReservationDTO dto)
        {
            var existingReservation = await _context.Reservations
                .Include(r => r.Event)
                .FirstOrDefaultAsync(r => r.Id == dto.Id);

            if (existingReservation == null)
            {
                return false;
            }
           
            var ev = await _context.Events.FirstOrDefaultAsync(e => e.Id == dto.EventId);

            if (ev == null)
            {
                return false;
            }          

            existingReservation.EventId = (int)dto.EventId;
            existingReservation.TicketNumber = dto.TicketNumber;
            existingReservation.Status = dto.Status;
            existingReservation.UserId = dto.UserId;
            existingReservation.TotalPrice = ev.Price * dto.TicketNumber;

            existingReservation.UpdatedOn = DateTime.Now;

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> CancelReservation(int id)
        {
            var reservation = await _context.Reservations.FindAsync(id);

            if (reservation == null)
                return false;

            reservation.Status = (int)ReservationStatus.canceled;
            await _context.SaveChangesAsync();

            return true;
        }

        public async Task<bool> DeleteReservation(int id)
        {
            var reservation = await _context.Reservations.FirstOrDefaultAsync(r => r.Id == id && r.Active);

            if (reservation == null)
            {
                return false;
            }
     
            reservation.Active = false;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<ReservationDTO> GetReservationById(int id)
        {
            var reservation = await _context.Reservations
                .Include(r => r.User)
                .Include(r => r.Event)
                    .ThenInclude(e => e.EventPerformers)
                        .ThenInclude(ep => ep.Performer)
                .Include(r => r.Event)
                    .ThenInclude(e => e.EventType)
                .Where(r => r.Active && r.Id == id)
                .FirstOrDefaultAsync();

            if (reservation == null)
                return null;

            return _mapper.Map<ReservationDTO>(reservation);
        }

        public async Task<ReservationPreviewDTO?> GetReservationByIdRest(int id)
        {
            var reservation = await _context.Reservations
           .Include(r => r.User)
           .Include(r => r.Event)
               .ThenInclude(e => e.EventType)
           .Include(r => r.Event)
               .ThenInclude(e => e.EventPerformers)
                   .ThenInclude(ep => ep.Performer)
           .FirstOrDefaultAsync(r => r.Id == id && r.Active == true);

            if (reservation == null)
                return null;

            return _mapper.Map<ReservationPreviewDTO>(reservation);
        }

        public async Task<int> CreateReservationRest(CreateReservationDTO dto, string userName)
        {
            if (dto == null)
                return 0;

            var user = await _userService.GetUserByUsername(userName);

            if (user == null)
                return 0;

            var eventEntity = await _context.Events
            .FirstOrDefaultAsync(e => e.Id == dto.EventId);

            if (eventEntity == null)
                return 0;

            var totalPrice = eventEntity.Price * dto.TicketNumber;

            var reservation = new Reservation
            {
                EventId = dto.EventId,
                TicketNumber = dto.TicketNumber,
                UserId = user.Id,
                Status = (int)ReservationStatus.active,
                TotalPrice = totalPrice,
                CreatedOn = DateTime.Now,
                Active = true
            };

            _context.Reservations.AddAsync(reservation);
            await _context.SaveChangesAsync();

            return reservation.Id;
        }


        public async Task<bool> UpdateReservationRest(EditReservationDTO dto)
        {
            var existingReservation = await _context.Reservations.FirstOrDefaultAsync(r => r.Id == dto.Id && r.Active);
            var user = await _userService.GetUserByUsername(dto.User);

            if (existingReservation == null)
                return false;

            existingReservation.EventId = dto.EventId;
            existingReservation.TicketNumber = dto.TicketNumber;
            existingReservation.UserId = user.Id;
            existingReservation.Status = dto.Status;
            existingReservation.CreatedOn = dto.CreatedOn;

            await _context.SaveChangesAsync();
            return true;
        }
    }
}
