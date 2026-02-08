using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Models.DTO.EventType;

namespace RWAEvent.BL.Services
{
    public interface IEventType
    {
        Task<List<EventTypeDTO>> GetAllEventTypes();
        Task<EventTypeResultDTO> GetEventTypesByFilter(EventTypeFilter filter);
        Task<EventTypeDTO?> GetEventTypeById(int id);
        Task<int> CreateEventType(EventTypeDTO dto);
        Task<bool> UpdateEventType(EventTypeDTO dto);
        Task<bool> DeleteEventType(int id);
    }
}
