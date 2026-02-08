using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.Performer
{
    public class EditPerformerDTO
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valId")]
        public int Id { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valName")]
        public string Name { get; set; }
        public string? LastName { get; set; }

        public override string ToString()
        {
            return $"Id: {Id}, Name: {Name}, LastName: {LastName}";
        }
    }
}
