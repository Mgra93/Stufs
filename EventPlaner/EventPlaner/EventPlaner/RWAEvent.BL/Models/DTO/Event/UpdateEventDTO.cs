using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Event
{
    public class UpdateEventDTO
    {
        [Required(ErrorMessage = "Id is required.")]
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
        public int EventTypeId { get; set; }
        [Required(ErrorMessage = "Performer list is required.")]
        public List<int> PerformerList { get; set; }

        public override string ToString()
        {
            var performers = PerformerList != null ? string.Join(",", PerformerList) : "null";
            return $"Id: {Id}, Title: {Title}, Price: {Price}, Time: {Time}, Location: {Location}, EventTypeId: {EventTypeId}, PerformerList: [{performers}]";
        }
    }
}
