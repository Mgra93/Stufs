using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.User
{
    public class ChangePasswordDTO
    {
        [Required(ErrorMessage = "User name is required.")]
        public string Username { get; set; }
        [Required(ErrorMessage = "Current password is required.")]
        [MinLength(8, ErrorMessage = "Current password must be at least 8 characters long.")]
        public string CurrentPassword { get; set; }
        [Required(ErrorMessage = "New password is required.")]
        [MinLength(8, ErrorMessage = "New password must be at least 8 characters long.")]
        public string NewPassword { get; set; }
        [Required(ErrorMessage = "Confirmed password required.")]
        [MinLength(8, ErrorMessage = "Confirmed password must be at least 8 characters long.")]
        public string ConfirmPassword { get; set; }
    }
}
