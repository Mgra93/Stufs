namespace RWAEvent.BL.Models;

public partial class Event
{
    public int Id { get; set; }

    public string Title { get; set; } = null!;

    public int EventTypeId { get; set; }

    public decimal Price { get; set; }

    public DateTime Time { get; set; }

    public string Location { get; set; } = null!;

    public bool Active { get; set; }

    public DateTime CreatedOn { get; set; }

    public DateTime? Updatedon { get; set; }

    public virtual ICollection<EventPerformer> EventPerformers { get; set; } = new List<EventPerformer>();

    public virtual EventType EventType { get; set; } = null!;

    public virtual ICollection<Reservation> Reservations { get; set; } = new List<Reservation>();
}
