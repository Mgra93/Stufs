namespace RWAEvent.BL.Models.DTO.Event
{
    public class EventRestResultDTO
    {
        public List<EventPreviewDTO> Events { get; set; } = new();
        public int TotalCount { get; set; }
    }
}
