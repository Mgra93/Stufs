namespace RWAEvent.BL.Models.DTO.Reservation
{
    public class ReservationRestResultDTO
    {
        public int TotalCount { get; set; }
        public List<ReservationPreviewDTO> Reservations { get; set; } = new();
    }
}
