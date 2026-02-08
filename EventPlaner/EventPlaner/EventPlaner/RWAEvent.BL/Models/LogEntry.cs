namespace RWAEvent.BL.Models;

public partial class LogEntry
{
    public int Id { get; set; }

    public DateTime CreatedOn { get; set; }

    public int Level { get; set; }

    public string Message { get; set; } = null!;
}
