using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class LoginVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUsername")]
        [Display(Name = "lblUsername", ResourceType = typeof(Resources.LoginVM))]
        public string Username { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPassword")]
        [MinLength(8, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPasswordSize")]
        [Display(Name = "lblPassword", ResourceType = typeof(Resources.LoginVM))]
        public string Password { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valLanguage")]
        public string SelectedLanguage { get; set; }
        public string? ReturnUrl { get; set; }
    }
}
