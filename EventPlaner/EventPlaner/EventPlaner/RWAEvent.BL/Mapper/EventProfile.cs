using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Event;

namespace RWAEvent.BL.Mapper
{
    public class EventProfile : Profile
    {
        public EventProfile()
        {
            CreateMap<Event, EventDTO>()
             .ForMember(
                 dest => dest.PerformerList,
                 opt => opt.MapFrom(src =>
                     src.EventPerformers.Select(ep => ep.Performer)
                 )
             ).ForMember(
                dest => dest.SelectedPerformerIds,
                opt => opt.MapFrom(src =>
                    src.EventPerformers.Select(ep => ep.PerformerId)
                )
             );

            CreateMap<EventDTO, Event>()
                .ForMember(
                    dest => dest.EventPerformers,
                    opt => opt.MapFrom(src =>
                        src.SelectedPerformerIds.Select(id => new EventPerformer
                        {
                            PerformerId = id
                        })
                    )
                )
                .ForMember(dest => dest.EventType, opt => opt.Ignore())
                .ForMember(dest => dest.Reservations, opt => opt.Ignore());

            CreateMap<Event, EventPreviewDTO>()
             .ForMember(
                 dest => dest.PerformerList,
                 opt => opt.MapFrom(src =>
                     src.EventPerformers.Select(ep => ep.Performer)
                 )
             );

            CreateMap<CreateEventDTO, Event>()
             .ForMember(e => e.EventPerformers,
                 o => o.MapFrom(d =>
                     d.PerformerList.Select(id => new EventPerformer
                     {
                         PerformerId = id
                     })
                 ));

            CreateMap<EventDTO, EventPreviewDTO>();
        }
    }
}
