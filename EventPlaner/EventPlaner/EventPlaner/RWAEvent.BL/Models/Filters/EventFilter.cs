using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.Filters
{
    public class EventFilter
    {
        [Display(Name = "lblEventType", ResourceType = typeof(Resources.Event))]
        public int? EventTypeId { get; set; }
        [Display(Name = "lblSearch", ResourceType = typeof(Resources.Event))]
        public string? Search { get; set; }
        public int Page { get; set; } = 1;
        public int PageSize { get; set; }
        [Display(Name = "lblDateFrom", ResourceType = typeof(Resources.Event))]
        public DateTime? DateFrom { get; set; }
        [Display(Name = "lblDateTo", ResourceType = typeof(Resources.Event))]
        public DateTime? DateTo { get; set; }
    }
}
