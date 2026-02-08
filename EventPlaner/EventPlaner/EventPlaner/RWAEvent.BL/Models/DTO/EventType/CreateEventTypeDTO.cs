using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.EventType
{
    public class CreateEventTypeDTO
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valName")]
        public string Name { get; set; }

        public override string ToString()
        {
            return $"Name: {Name}";
        }
    }
}
