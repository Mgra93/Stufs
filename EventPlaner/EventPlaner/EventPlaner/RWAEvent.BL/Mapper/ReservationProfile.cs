using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.Reservation;

namespace RWAEvent.BL.Mapper
{
    public class ReservationProfile : Profile
    {
        public ReservationProfile()
        {
            CreateMap<Reservation, ReservationPreviewDTO>();
            CreateMap<Reservation, ReservationDTO>();
            CreateMap<ReservationDTO, ReservationPreviewDTO>();
        }
    }
}
