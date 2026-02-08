using Microsoft.EntityFrameworkCore;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.Filters;
using AutoMapper;
using RWAEvent.BL.Models.DTO.EventType;

namespace RWAEvent.BL.Services
{
    public class EventTypeService : IEventType
    {
        private readonly RwaeventContext _context;
        private readonly IMapper _mapper;

        public EventTypeService(RwaeventContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }


        public async Task<List<EventTypeDTO>> GetAllEventTypes()
        {
            var eventTypeList = await _context.EventTypes
              .Where(et => et.Active)
              .OrderBy(et => et.Name)
              .ToListAsync();
            var dtoList = _mapper.Map<List<EventTypeDTO>>(eventTypeList);
            return dtoList;
        }

        public async Task<EventTypeResultDTO> GetEventTypesByFilter(EventTypeFilter filter)
        {
            var query = _context.EventTypes
                .Where(et => et.Active)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(filter.Search))
            {
                var searchTerm = filter.Search.Trim().ToLower();
                query = query.Where(et => et.Name.ToLower().Contains(searchTerm));
            }

            var count = await query.CountAsync();

            int skip = (filter.Page - 1) * filter.PageSize;

            var eventTypeList = await query
                .OrderBy(t => t.Name)
                .Skip(skip)
                .Take(filter.PageSize)
                .ToListAsync();

            var dtoList = _mapper.Map<List<EventTypeDTO>>(eventTypeList);

            return new EventTypeResultDTO
            {
                TotalCount = count,
                EventTypes = dtoList
            };
        }

        public async Task<EventTypeDTO?> GetEventTypeById(int id)
        {
            var eventType = await _context.EventTypes
                .Where(e => e.Id == id && e.Active)
                .FirstOrDefaultAsync();

            if (eventType == null)
                return null;

            return _mapper.Map<EventTypeDTO>(eventType);
        }

        public async Task<int> CreateEventType(EventTypeDTO dto)
        {
            var eventType = _mapper.Map<EventType>(dto);
            eventType.Active = true;
            eventType.CreatedOn = DateTime.Now;

            _context.EventTypes.Add(eventType);
            await _context.SaveChangesAsync();
            return eventType.Id;
        }

        public async Task<bool> UpdateEventType(EventTypeDTO dto)
        {
            var eventType = await _context.EventTypes.FirstOrDefaultAsync(e => e.Id == dto.Id && e.Active);
            if (eventType == null)
                return false;

            eventType.Name = dto.Name;
            eventType.UpdatedOn = DateTime.Now;
            await _context.SaveChangesAsync();

            return true;
        }

        public async Task<bool> DeleteEventType(int id)
        {
            var eventType = await _context.EventTypes
                .FirstOrDefaultAsync(e => e.Id == id && e.Active);

            if (eventType == null)
            {
                return false;
            }
             
            eventType.Active = false;
            await _context.SaveChangesAsync();
            return true;
        }
    }
}
