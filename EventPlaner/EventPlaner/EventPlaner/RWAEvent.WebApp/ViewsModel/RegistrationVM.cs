using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class RegistrationVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUsername")]
        [Display(Name = "lblUserName", ResourceType = typeof(Resources.RegistrationVM))]
        public string Username { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmail")]
        [EmailAddress(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmailFormat")]
        [Display(Name = "lblEmail", ResourceType = typeof(Resources.RegistrationVM))]
        public string Email { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFirstName")]
        [Display(Name = "lblFirstName", ResourceType = typeof(Resources.RegistrationVM))]
        public string FirstName { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valLastName")]
        [Display(Name = "lblLastName", ResourceType = typeof(Resources.RegistrationVM))]
        public string LastName { get; set; }
        [Display(Name = "lblPhone", ResourceType = typeof(Resources.RegistrationVM))]
        public string? Phone { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPasswordSize")]
        [Display(Name = "lblPassword", ResourceType = typeof(Resources.RegistrationVM))]
        public string Password { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRepeatPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRepeatPasswordSize")]
        [Compare("Password", ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRepeatPasswordMismatch")]
        [Display(Name = "lblRepeatPassword", ResourceType = typeof(Resources.RegistrationVM))]
        public string RepeatPassword { get; set; }
    }
}
