using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.User
{
    public class LoginDTO
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUsername")]
        public string Username { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPasswordSize")]
        public string Password { get; set; }

        public override string ToString()
        {
            return $"Username: {Username}, Password: {Password}";
        }
    }
}
