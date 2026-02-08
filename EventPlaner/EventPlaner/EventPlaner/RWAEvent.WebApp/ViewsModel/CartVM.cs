using RWAEvent.BL.Models.DTO.Reservation;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class CartVM
    {
        public List<ReservationDTO> Reservations { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTotal")]
        [Display(Name = "lblTotal", ResourceType = typeof(Resources.CartVM))]
        public decimal Total => Reservations?.Sum(r => (r.Event?.Price ?? 0) * r.TicketNumber) ?? 0;
    }
}
