using System;
using System.Collections.Generic;

namespace RWAEvent.BL.Models;

public partial class Reservation
{
    public int Id { get; set; }

    public int EventId { get; set; }

    public int TicketNumber { get; set; }

    public int UserId { get; set; }

    public int Status { get; set; }

    public decimal TotalPrice { get; set; }

    public DateTime CreatedOn { get; set; }

    public DateTime? UpdatedOn { get; set; }

    public bool Active { get; set; }

    public virtual Event Event { get; set; } = null!;

    public virtual User User { get; set; } = null!;
}
