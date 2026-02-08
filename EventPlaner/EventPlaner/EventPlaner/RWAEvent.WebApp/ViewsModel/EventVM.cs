using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.Filters;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class EventVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valCurrentPage")]
        public int CurrentPage { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTotalPages")]
        public int TotalPages { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFilter")]
        public EventFilter Filter { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEvents")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEvents")]
        public List<EventDTO> Events { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventTypes")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventTypes")]
        public List<EventTypeDTO> EventTypes { get; set; }
    }
}
