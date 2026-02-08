using RWAEvent.BL.Models.DTO.Reservation;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public interface IReservation
    {
        Task<int> CreateReservations(List<ReservationDTO> reservationList, String userName);
        Task<bool> UpdateReservation(ReservationDTO dto);
        Task<bool> CancelReservation(int id);
        Task<bool> DeleteReservation(int id);
        Task<ReservationDTO?> GetReservationById(int id);
        Task<List<ReservationDTO>> GetReservationsByFilter(ReservationFilter filter);
        Task<ReservationPreviewDTO?> GetReservationByIdRest(int id);
        Task<int> CreateReservationRest(CreateReservationDTO dto, string userName);
        Task<bool> UpdateReservationRest(EditReservationDTO dto);
    }
}
