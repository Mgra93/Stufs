using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Performer
{
    public class CreatePerformerDTO
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valName")]
        public string Name { get; set; }
        public string? LastName { get; set; }
    }
}
