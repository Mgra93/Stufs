using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;
using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.DTO.Performer;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Event
{
    public class EventDTO
    {
        public int Id { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTitle")]
        [Display(Name = "lblTitle", ResourceType = typeof(Resources.Event))]
        public string Title { get; set; } = null!;
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEventType")]
        [Display(Name = "lblEventType", ResourceType = typeof(Resources.Event))]
        public int? EventTypeId { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPrice")]
        [Display(Name = "lblPrice", ResourceType = typeof(Resources.Event))]
        public decimal? Price { get; set; } = 0.00m;
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTime")]
        [Display(Name = "lblTime", ResourceType = typeof(Resources.Event))]
        [DisplayFormat(DataFormatString = "{0:dd. MM. yyyy. HH:mm}")]
        public DateTime Time { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valLocation")]
        [Display(Name = "lblLocation", ResourceType = typeof(Resources.Event))]
        public string Location { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valSelectedPerformerIds")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valSelectedPerformerIds")]
        [Display(Name = "lblSelectedPerformer", ResourceType = typeof(Resources.Event))]
        public List<int> SelectedPerformerIds { get; set; } = new List<int>();
        [ValidateNever]
        public EventTypeDTO EventType { get; set; }
        [ValidateNever]
        public List<PerformerDTO> PerformerList { get; set; } = new List<PerformerDTO>();
        [ValidateNever]
        public List<EventTypeDTO> EventTypeList { get; set; } = new List<EventTypeDTO>();

    }
}
