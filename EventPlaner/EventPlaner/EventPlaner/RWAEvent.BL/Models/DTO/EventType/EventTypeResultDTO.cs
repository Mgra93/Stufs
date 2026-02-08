namespace RWAEvent.BL.Models.DTO.EventType
{
    public class EventTypeResultDTO
    {
        public List<EventTypeDTO> EventTypes { get; set; } = new();
        public int TotalCount { get; set; }
    }
}
