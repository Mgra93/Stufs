using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.Filters;

namespace RWAEvent.BL.Services
{
    public interface IEvent
    {
        Task<int> CreateEvent(EventDTO dto);
        Task<List<EventDTO>> GetAllEvents();
        Task<EventDTO?> GetEventById(int id);
        Task<List<EventDTO>> GetEventsByType(int eventTypeId);
        Task<EventResultDTO> GetEventsByFilter(EventFilter filter);
        Task<bool> UpdateEvent(EventDTO dto);
        Task<bool> DeleteEvent(int id);
        Task<EventPreviewDTO?> GetEventByIdRest(int id);
        Task<int> CreateEventRest(CreateEventDTO dto);
        Task<bool> UpdateEventRest(UpdateEventDTO dto);
    }
}
