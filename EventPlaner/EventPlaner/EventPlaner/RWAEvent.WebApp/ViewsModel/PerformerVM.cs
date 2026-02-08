using RWAEvent.BL.Models.DTO.Performer;
using RWAEvent.BL.Models.Filters;
using System.ComponentModel.DataAnnotations;

namespace RWAEvent.WebApp.ViewsModel
{
    public class PerformerVM
    {
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPerformers")]
        [MinLength(1, ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPerformersMin")]
        public List<PerformerDTO> Performers { get; set; }

        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valCurrentPage")]
        public int CurrentPage { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valTotalPages")]
        public int TotalPages { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valPageSize")]
        public int PageSize { get; set; }
        [Required(ErrorMessageResourceType = typeof(Resources.ValidationMessages), ErrorMessageResourceName = "valFilter")]
        public PerformerFilter Filter { get; set; }
    }
}
