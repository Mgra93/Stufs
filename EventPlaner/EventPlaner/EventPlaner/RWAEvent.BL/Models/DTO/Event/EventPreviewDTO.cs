using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.DTO.Performer;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Event
{
    public class EventPreviewDTO
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Title is required.")]
        public string Title { get; set; } = null!;
        [Required(ErrorMessage = "Price is required.")]
        public decimal Price { get; set; }
        [Required(ErrorMessage = "Time is required.")]
        public DateTime Time { get; set; }
        [Required(ErrorMessage = "Location is required.")]
        public string Location { get; set; }
        [Required(ErrorMessage = "EventType is required.")]
        public EventTypeDTO EventType { get; set; }
        [Required(ErrorMessage = "Performer list is required.")]
        public List<PerformerDTO> PerformerList { get; set; } = new List<PerformerDTO>();
    }
}
