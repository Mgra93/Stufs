using AutoMapper;
using RWAEvent.BL.Models;
using RWAEvent.BL.Models.DTO.User;

namespace RWAEvent.BL.Mapper
{
    public class UserProfile : Profile
    {
        public UserProfile()
        {
            CreateMap<User, UserDTO>();
            CreateMap<User, UserPreviewDTO>();
            CreateMap<UserDTO, UserPreviewDTO>();
            CreateMap<RegistrationDTO, User>();
        }
    }
}
