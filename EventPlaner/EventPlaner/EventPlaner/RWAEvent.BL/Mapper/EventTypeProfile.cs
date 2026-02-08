using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.EventType;

namespace RWAEvent.BL.Mapper
{
    public class EventTypeProfile : Profile
    {
        public EventTypeProfile()
        {
            CreateMap<EventType, EventTypeDTO>();
            CreateMap<EventTypeDTO, EventType>();
        }
    }
}
