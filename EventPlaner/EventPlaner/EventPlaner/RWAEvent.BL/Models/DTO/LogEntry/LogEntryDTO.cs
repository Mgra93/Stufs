namespace RWAEvent.BL.Models.DTO.LogEntry
{
    public class LogEntryDTO
    {
        public DateTime CreatedOn { get; set; }

        public string Level { get; set; }

        public string Message { get; set; } = null!;
    }
}
