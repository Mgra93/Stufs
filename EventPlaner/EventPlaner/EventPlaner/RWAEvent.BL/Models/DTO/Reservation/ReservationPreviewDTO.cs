using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.User;

namespace RWAEvent.BL.Models.DTO.Reservation
{
    public class ReservationPreviewDTO
    {
        public int Id { get; set; }
        public int TicketNumber { get; set; }
        public int Status { get; set; }
        public decimal TotalPrice { get; set; }
        public DateTime CreatedOn { get; set; }
        public virtual EventPreviewDTO Event { get; set; } = null!;
        public virtual UserPreviewDTO User { get; set; } = null!;
    }
}
