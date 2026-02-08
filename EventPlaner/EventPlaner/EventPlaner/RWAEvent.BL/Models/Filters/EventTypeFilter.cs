using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.Filters
{
    public class EventTypeFilter
    {
        [Display(Name = "lblSearch", ResourceType = typeof(Resources.EventType))]
        public string? Search { get; set; }
        public int Page { get; set; } = 1;
        public int PageSize { get; set; } = 10;
    }
}
