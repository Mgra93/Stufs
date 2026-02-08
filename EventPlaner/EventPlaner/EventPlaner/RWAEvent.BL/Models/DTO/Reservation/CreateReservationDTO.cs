using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Reservation
{
    public class CreateReservationDTO
    {
        [Required(ErrorMessage = "Event id number on is required")]
        public int EventId { get; set; }
        [Required(ErrorMessage = "Ticket number on is required")]
        [Range(1, int.MaxValue, ErrorMessage = "Ticket number must be positive number.")]
        public int TicketNumber { get; set; }
        [Required(ErrorMessage = "User on is required")]
        public string User { get; set; }

        public override string ToString()
        {
            return $"EventId: {EventId}, TicketNumber: {TicketNumber}, User: {User}";
        }
    }
}
