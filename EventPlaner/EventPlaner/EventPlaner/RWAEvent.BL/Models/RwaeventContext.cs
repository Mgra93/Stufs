using Microsoft.EntityFrameworkCore;

namespace RWAEvent.BL.Models;

public partial class RwaeventContext : DbContext
{
    public RwaeventContext()
    {
    }

    public RwaeventContext(DbContextOptions<RwaeventContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Event> Events { get; set; }

    public virtual DbSet<EventPerformer> EventPerformers { get; set; }

    public virtual DbSet<EventType> EventTypes { get; set; }

    public virtual DbSet<LogEntry> LogEntries { get; set; }

    public virtual DbSet<Performer> Performers { get; set; }

    public virtual DbSet<Reservation> Reservations { get; set; }

    public virtual DbSet<User> Users { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder) {}

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Event>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Event__3214EC0735E6E0DD");

            entity.ToTable("Event");

            entity.Property(e => e.Active).HasDefaultValue(true);
            entity.Property(e => e.Location).HasMaxLength(500);
            entity.Property(e => e.Price).HasColumnType("decimal(10, 2)");
            entity.Property(e => e.Title).HasMaxLength(200);

            entity.HasOne(d => d.EventType).WithMany(p => p.Events)
                .HasForeignKey(d => d.EventTypeId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Event__EventType__4D94879B");
        });

        modelBuilder.Entity<EventPerformer>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__EventPer__3214EC0710DB8A7D");

            entity.ToTable("EventPerformer");

            entity.HasOne(d => d.Event).WithMany(p => p.EventPerformers)
                .HasForeignKey(d => d.EventId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__EventPerf__Event__534D60F1");

            entity.HasOne(d => d.Performer).WithMany(p => p.EventPerformers)
                .HasForeignKey(d => d.PerformerId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__EventPerf__Perfo__5441852A");
        });

        modelBuilder.Entity<EventType>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__EventTyp__3214EC078ED12BF9");

            entity.ToTable("EventType");

            entity.Property(e => e.Active).HasDefaultValue(true);
            entity.Property(e => e.Name).HasMaxLength(100);
        });

        modelBuilder.Entity<LogEntry>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__LogEntry__3214EC0751031D1D");

            entity.ToTable("LogEntry");

            entity.Property(e => e.Message).HasMaxLength(3000);
        });

        modelBuilder.Entity<Performer>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Performe__3214EC077EEAEC81");

            entity.ToTable("Performer");

            entity.Property(e => e.Active).HasDefaultValue(true);
            entity.Property(e => e.LastName).HasMaxLength(100);
            entity.Property(e => e.Name).HasMaxLength(100);
        });

        modelBuilder.Entity<Reservation>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Reservat__3214EC07C289570D");

            entity.ToTable("Reservation");

            entity.Property(e => e.Active).HasDefaultValue(true);

            entity.HasOne(d => d.Event).WithMany(p => p.Reservations)
                .HasForeignKey(d => d.EventId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Reservati__Event__5AEE82B9");

            entity.HasOne(d => d.User).WithMany(p => p.Reservations)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("FK__Reservati__UserI__5BE2A6F2");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__User__3214EC0781FD916E");

            entity.ToTable("User");

            entity.Property(e => e.Email).HasMaxLength(256);
            entity.Property(e => e.FirstName).HasMaxLength(256);
            entity.Property(e => e.LastName).HasMaxLength(256);
            entity.Property(e => e.Phone).HasMaxLength(256);
            entity.Property(e => e.PwdHash).HasMaxLength(256);
            entity.Property(e => e.PwdSalt).HasMaxLength(256);
            entity.Property(e => e.Username).HasMaxLength(50);
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
