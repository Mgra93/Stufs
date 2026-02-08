namespace RWAEvent.BL.Models.DTO.Event
{
    public class EventResultDTO
    {
        public List<EventDTO> Events { get; set; } = new();
        public int TotalCount { get; set; }
    }
}
