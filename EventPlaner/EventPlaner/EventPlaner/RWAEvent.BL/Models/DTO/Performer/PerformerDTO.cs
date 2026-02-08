using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Performer
{
    public class PerformerDTO
    {
        public int Id { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valName")]
        [Display(Name = "lblName", ResourceType = typeof(Resources.Performer))]
        public string Name { get; set; }
        [Display(Name = "lblLastName", ResourceType = typeof(Resources.Performer))]
        public string? LastName { get; set; }
    }
}
