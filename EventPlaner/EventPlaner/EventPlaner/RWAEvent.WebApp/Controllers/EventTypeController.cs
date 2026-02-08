using RWAEvent.BL.Models.Filters;
using RWAEvent.WebApp.ViewsModel;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Services;
using RWAEvent.BL.Models.DTO.EventType;

namespace RWAEvent.WebApp.Controllers
{
    public class EventTypeController : Controller
    {
        private readonly IEventType _eventTypeService;

        public EventTypeController(IEventType eventTypeService)
        {
            _eventTypeService = eventTypeService;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> List(string? search, int page = 1, int pageSize = 10)
        {
            var filter = new EventTypeFilter
            {
                Search = search,
                Page = page,
                PageSize = pageSize
            };

            var result = await _eventTypeService.GetEventTypesByFilter(filter);

            var model = new EventTypeVM
            {
                EventTypes = result.EventTypes,
                CurrentPage = page,
                TotalPages = (int)Math.Ceiling((double)result.TotalCount / pageSize),
                Filter = filter
            };

            return View(model);
        }

        [HttpPost]
        [Authorize]
        public IActionResult List(EventTypeVM viewModel)
        {
            var filter = viewModel.Filter ?? new EventTypeFilter();

            return RedirectToAction("List", new
            {
                page = 1,
                pageSize = filter.PageSize,
                search = filter.Search
            });
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public IActionResult Create()
        {
            return View();
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Create(EventTypeDTO dto)
        {
            if (!ModelState.IsValid)
            {
                return View(dto);
            }

            await _eventTypeService.CreateEventType(dto);

            return RedirectToAction("List");
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Delete(int id)
        {
            bool isDeleted = await _eventTypeService.DeleteEventType(id);
            if (!isDeleted)
            {
                return NotFound();
            }

            return RedirectToAction("List");
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(int id)
        {
            var eventType = await _eventTypeService.GetEventTypeById(id);
            return View(eventType);
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(EventTypeDTO dto)
        {
            if (!ModelState.IsValid)
            {
                return View(dto);
            }

            bool isUpdated = await _eventTypeService.UpdateEventType(dto);
            if (!isUpdated)
                return NotFound();

            return RedirectToAction("List");
        }
    }
}
