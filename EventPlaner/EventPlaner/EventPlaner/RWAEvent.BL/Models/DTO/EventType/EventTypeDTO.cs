using System.ComponentModel.DataAnnotations;

namespace RWAEvent.BL.Models.DTO.EventType
{
    public class EventTypeDTO
    {
        public int Id { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valName")]
        [Display(Name = "lblName", ResourceType = typeof(Resources.EventType))]
        public string Name { get; set; }

        public override string ToString()
        {
            return $"Id: {Id}, Name: {Name}";
        }
    }
}
