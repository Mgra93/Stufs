using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.Filters;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class EventTypeVM
    {

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventTypes")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventTypes")]
        public List<EventTypeDTO> EventTypes { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valCurrentPage")]
        public int CurrentPage { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTotalPages")]
        public int TotalPages { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFilter")]
        public EventTypeFilter Filter { get; set; }
    }
}
