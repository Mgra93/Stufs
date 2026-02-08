namespace RWAEvent.BL.Models.Filters
{
    public class ReservationFilter
    {
        public string? Search { get; set; }
        public string? User { get; set; }
        public int Page { get; set; } = 1;
        public int PageSize { get; set; } = 10;
    }
}
