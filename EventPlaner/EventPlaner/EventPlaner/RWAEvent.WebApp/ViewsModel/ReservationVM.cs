using RWAEvent.BL.Models.DTO.Reservation;
using RWAEvent.BL.Models.Filters;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class ReservationVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valCurrentPage")]
        public int CurrentPage { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTotalPages")]
        public int TotalPages { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFilter")]
        public ReservationFilter Filter { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valReservations")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valReservations")]
        public List<ReservationDTO> Reservations { get; set; }
    }
}
