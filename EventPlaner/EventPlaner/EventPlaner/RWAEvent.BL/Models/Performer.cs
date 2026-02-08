namespace RWAEvent.BL.Models;

public partial class Performer
{
    public int Id { get; set; }

    public string Name { get; set; } = null!;

    public string? LastName { get; set; }

    public bool Active { get; set; }

    public DateTime CreatedOn { get; set; }

    public DateTime? UpdatedOn { get; set; }

    public virtual ICollection<EventPerformer> EventPerformers { get; set; } = new List<EventPerformer>();
}
