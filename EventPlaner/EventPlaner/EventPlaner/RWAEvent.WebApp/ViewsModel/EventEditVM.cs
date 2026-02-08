using System.ComponentModel.DataAnnotations;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.DTO.Performer;

namespace RWAEvent.WebApp.ViewsModel
{
    public class EventEditVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEvent")]
        public EventDTO Event { get; set; }
        public List<EventTypeDTO> EventTypes { get; set; } = new();
        public List<PerformerDTO> PerformerList { get; set; } = new();
    }
}
