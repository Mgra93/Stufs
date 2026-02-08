using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.LogEntry;
using RWAEvent.BL.Models.Enums;

namespace RWAEvent.BL.Mapper
{
    public class LogEntryProfile : Profile
    {
        public LogEntryProfile()
        {
            CreateMap<LogEntry, LogEntryDTO>()
                .ForMember(
                    d => d.Level,
                    o => o.MapFrom(s => ((LogLvl)s.Level).ToString())
                );
        }
    }
}
