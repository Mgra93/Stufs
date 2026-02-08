namespace RWAEvent.BL.Models;

public partial class EventType
{
    public int Id { get; set; }

    public string Name { get; set; } = null!;

    public bool Active { get; set; }

    public DateTime CreatedOn { get; set; }

    public DateTime? UpdatedOn { get; set; }

    public virtual ICollection<Event> Events { get; set; } = new List<Event>();
}
