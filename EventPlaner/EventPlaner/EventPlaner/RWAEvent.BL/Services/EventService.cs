using AutoMapper;
using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public class EventService : IEvent
    {
        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;

        public EventService(RwaeventContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<List<EventDTO>> GetAllEvents()
        {
            var eventList = await _context.Events
                .Include(e => e.EventType)
                .Include(e => e.EventPerformers)
                  .ThenInclude(ep => ep.Performer)
                .Where(e => e.Active)
                .ToListAsync();
            var dtoList = _mapper.Map<List<EventDTO>>(eventList);
            return dtoList;
        }

        public async Task<EventDTO?> GetEventById(int id)
        {
            var evn = await _context.Events
                .Include(ev => ev.EventType)
                .Include(ev => ev.EventPerformers)
                    .ThenInclude(ep => ep.Performer)
                .FirstOrDefaultAsync(ev => ev.Id == id && ev.Active == true);

            if (evn == null)
                return null;

            var dto = _mapper.Map<EventDTO>(evn);
            return dto;
        }

        public async Task<List<EventDTO>> GetEventsByType(int eventTypeId)
        {
            var eventList = await _context.Events
                .Include(e => e.EventType)
                .Include(e => e.EventPerformers)
                    .ThenInclude(ep => ep.Performer)
                .Where(e => e.Active && e.EventTypeId == eventTypeId)
                .ToListAsync();
            var dtoList = _mapper.Map<List<EventDTO>>(eventList);
            return dtoList;
        }

        public async Task<EventResultDTO> GetEventsByFilter(EventFilter filter)
        {
            var query = _context.Events
                .Include(e => e.EventType)
                .Include(e => e.EventPerformers)
                    .ThenInclude(ep => ep.Performer)
                .Where(e => e.Active)
                .AsQueryable();

            if (filter.EventTypeId.HasValue)
            {
                query = query.Where(e => e.EventTypeId == filter.EventTypeId.Value);
            }

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                var searchTerm = filter.Search.Trim().ToLower();
                query = query.Where(e => e.Title.ToLower().Contains(searchTerm));
            }

            if (filter.DateFrom.HasValue)
            {
                query = query.Where(e => e.Time >= filter.DateFrom.Value.Date);
            }

            if (filter.DateTo.HasValue)
            {
                query = query.Where(e => e.Time < filter.DateTo.Value.Date.AddDays(1));
            }

            var totalCount = await query.CountAsync();
            int skip = (filter.Page - 1) * filter.PageSize;

            var eventList = await query.OrderBy(e => e.Title).Skip(skip).Take(filter.PageSize).ToListAsync();
            var eventListPreview = _mapper.Map<List<EventDTO>>(eventList);

            return new EventResultDTO
            {
                TotalCount = totalCount,
                Events = eventListPreview
            };
        }

        public async Task<int> CreateEvent(EventDTO dto)
        {
            var newEvent = _mapper.Map<Event>(dto);
            newEvent.Active = true;
            newEvent.CreatedOn = DateTime.Now;
            _context.Events.Add(newEvent);
            await _context.SaveChangesAsync();

            return newEvent.Id;
        }

        public async Task<bool> UpdateEvent(EventDTO dto)
        {
            var existingEvent = await _context.Events
                .Include(e => e.EventPerformers)
                .FirstOrDefaultAsync(e => e.Id == dto.Id);

            if (existingEvent == null)
                return false;

            existingEvent.Title = dto.Title;
            existingEvent.EventTypeId = (int)dto.EventTypeId;
            existingEvent.Price = (decimal)dto.Price;
            existingEvent.Time = dto.Time;
            existingEvent.Location = dto.Location;
            existingEvent.Updatedon = DateTime.Now;

            _context.EventPerformers.RemoveRange(existingEvent.EventPerformers);

            existingEvent.EventPerformers = dto.PerformerList
                .Select(p => new EventPerformer
                {
                    PerformerId = p.Id,
                    EventId = dto.Id
                }).ToList();

            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteEvent(int id)
        {
            var existingEvent = await _context.Events
                .Include(ev => ev.EventPerformers)
                .Include(ev => ev.Reservations)
                .FirstOrDefaultAsync(ev => ev.Id == id && ev.Active == true);

            if (existingEvent == null)
            {
                return false;
            }

            existingEvent.Active = false;
            await _context.SaveChangesAsync();
            return true;
        }


        //REST
        public async Task<EventPreviewDTO?> GetEventByIdRest(int id)
        {
            var e = await _context.Events
                .Include(ev => ev.EventType)
                .Include(ev => ev.EventPerformers)
                    .ThenInclude(ep => ep.Performer)
                .FirstOrDefaultAsync(ev => ev.Id == id && ev.Active);

            if (e == null)
                return null;

            return _mapper.Map<EventPreviewDTO>(e);
        }

        public async Task<int> CreateEventRest(CreateEventDTO dto)
        {
            var newEvent = _mapper.Map<Event>(dto);
            newEvent.Active = true;
            newEvent.CreatedOn = DateTime.Now;

            newEvent.EventPerformers = dto.PerformerList
                .Select(pid => new EventPerformer
                {
                    PerformerId = pid
                })
                .ToList();

            _context.Events.Add(newEvent);
            await _context.SaveChangesAsync();

            return newEvent.Id;
        }


        public async Task<bool> UpdateEventRest(UpdateEventDTO dto)
        {
            var existingEvent = await _context.Events
                .Include(e => e.EventPerformers)
                .FirstOrDefaultAsync(e => e.Id == dto.Id && e.Active);

            if (existingEvent == null)
                return false;

            existingEvent.Title = dto.Title;
            existingEvent.EventTypeId = dto.EventTypeId;
            existingEvent.Price = dto.Price;
            existingEvent.Time = dto.Time;
            existingEvent.Location = dto.Location;
            existingEvent.Updatedon = DateTime.Now;

            _context.EventPerformers.RemoveRange(existingEvent.EventPerformers);

            existingEvent.EventPerformers = dto.PerformerList
                .Select(p => new EventPerformer
                {
                    PerformerId = p,
                    EventId = dto.Id
                }).ToList();

            await _context.SaveChangesAsync();
            return true;
        }
    }
}
