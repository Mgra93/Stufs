using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.User
{
    public class UpdateUserDTO
    {
        public int Id { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valUsername")]
        [Display(Name = "lblUsername", ResourceType = typeof(Resources.User))]
        public string Username { get; set; } = null!;

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFirstName")]
        [Display(Name = "lblFirstName", ResourceType = typeof(Resources.User))]
        public string FirstName { get; set; } = null!;

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valLastName")]
        [Display(Name = "lblLastName", ResourceType = typeof(Resources.User))]
        public string LastName { get; set; } = null!;

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmail")]
        [Display(Name = "lblEmail", ResourceType = typeof(Resources.User))]
        [EmailAddress(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valEmailFormat")]
        public string Email { get; set; } = null!;
        [Display(Name = "lblPhone", ResourceType = typeof(Resources.User))]
        [Phone(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPhoneFormat")]
        public string? Phone { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valRole")]
        [Display(Name = "lblRole", ResourceType = typeof(Resources.User))]
        public int Role { get; set; }
        [Display(Name = "lblOldPassword", ResourceType = typeof(Resources.User))]
        public string OldPassword { get; set; } = null!;
        [Display(Name = "lblNewPassword", ResourceType = typeof(Resources.User))]
        public string NewPassword { get; set; } = null!;
        public bool ChangePassword { get; set; } = false;
    }
}
