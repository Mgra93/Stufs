using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Performer;

namespace RWAEvent.BL.Mapper
{
    public class PerformerProfile : Profile
    {
        public PerformerProfile()
        {
            CreateMap<Performer, PerformerDTO>();
            CreateMap<PerformerDTO, Performer>();
        }
    }
}
