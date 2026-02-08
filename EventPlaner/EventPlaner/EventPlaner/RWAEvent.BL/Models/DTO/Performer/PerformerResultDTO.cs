namespace RWAEvent.BL.Models.DTO.Performer
{
    public class PerformerResultDTO
    {
        public List<PerformerDTO> Performers { get; set; } = new();
        public int TotalCount { get; set; }
    }
}
