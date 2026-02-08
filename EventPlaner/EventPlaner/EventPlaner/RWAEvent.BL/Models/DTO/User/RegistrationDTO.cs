using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.User
{
    public class RegistrationDTO
    {

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUsername")]
        public string Username { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmail")]
        [EmailAddress(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmailFormat")]
        public string Email { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFirstName")]
        public string FirstName { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valLastName")]
        public string LastName { get; set; }
        [Phone(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPhoneFormat")]
        public string? Phone { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPasswordSize")]
        public string Password { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRepeatPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRepeatPasswordSize")]
        public string RepeatPassword { get; set; }

        public override string ToString()
        {
            return $"Username: {Username}, Email: {Email}, FirstName: {FirstName}, LastName: {LastName}, Phone: {Phone}";
        }
    }
}
