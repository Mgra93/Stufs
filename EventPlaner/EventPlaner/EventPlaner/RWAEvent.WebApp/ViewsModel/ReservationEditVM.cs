using System.ComponentModel.DataAnnotations;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.DTO.Reservation;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Enums;

namespace RWAEvent.WebApp.ViewsModel
{
    public class ReservationEditVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valReservation")]
        public ReservationDTO Reservation { get; set; } = new();
        public List<UserDTO> UserList { get; set; } = new();
        public List<EventDTO> EventList { get; set; } = new();
        public List<EventTypeDTO> EventTypeList { get; set; } = new();
        public List<string> StatusList => Enum.GetValues(typeof(ReservationStatus)).Cast<ReservationStatus>().Select(s => s.ToString()).ToList();
        [Display(Name = "Tip događaja")]
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventType")]
        public int? SelectedEventTypeId { get; set; }
    }
}
