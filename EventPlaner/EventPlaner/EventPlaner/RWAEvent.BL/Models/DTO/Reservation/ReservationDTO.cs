using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.User;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Reservation
{
    public class ReservationDTO
    {
        public int Id { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEvent")]
        [Display(Name = "lblEvent", ResourceType = typeof(Resources.Reservation))]
        public int? EventId { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTicketNumber")]
        [Display(Name = "lblTicketNumber", ResourceType = typeof(Resources.Reservation))]
        public int TicketNumber { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUser")]
        [Display(Name = "lblUser", ResourceType = typeof(Resources.Reservation))]
        public int UserId { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valStatus")]
        [Display(Name = "lblStatus", ResourceType = typeof(Resources.Reservation))]
        public int Status { get; set; }
        [Display(Name = "lblTotalPrice", ResourceType = typeof(Resources.Reservation))]
        public decimal TotalPrice { get; set; }
        [Display(Name = "lblCreatedOn", ResourceType = typeof(Resources.Reservation))]
        [DisplayFormat(DataFormatString = "{0:dd.MM.yyyy HH:mm}")]
        public DateTime CreatedOn { get; set; }
        [ValidateNever]
        public virtual EventDTO Event { get; set; } = null!;

        [ValidateNever]
        public virtual UserDTO User { get; set; } = null!;
    }
}
