using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.Performer;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.WebApp.ViewsModel;
using System.Resources;

namespace RWAEvent.WebApp.Controllers
{
    public class EventController : Controller
    {
        private readonly IEvent _eventService;
        private readonly IEventType _eventTypeService;
        private readonly IPerformer _performerService;

        public EventController(IEvent eventService, IEventType eventTypeService, IPerformer performerService)
        {
            _eventService = eventService;
            _eventTypeService = eventTypeService;
            _performerService = performerService;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> List()
        {
            var viewModel = new EventVM
            {
                EventTypes = await _eventTypeService.GetAllEventTypes()
            };
            return View(viewModel);
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> GetEventList(int page = 1, int pageSize = 6, int? eventTypeId = null, string? search = null, DateTime? dateFrom = null, DateTime? dateTo = null)
        {
            var filter = new EventFilter
            {
                Page = page,
                PageSize = pageSize,
                EventTypeId = eventTypeId,
                Search = search,
                DateFrom = dateFrom,
                DateTo = dateTo
            };

            var result = await _eventService.GetEventsByFilter(filter);

            var viewModel = new EventVM
            {
                Events = result.Events,
                CurrentPage = page,
                TotalPages = (int)Math.Ceiling(result.TotalCount / (double)pageSize)
            };

            return PartialView("EventCard", viewModel);
        }

        [HttpGet]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Create()
        {
            var listEventTypes = await _eventTypeService.GetAllEventTypes();
            var listPerformers = await _performerService.GetAllPerformers();
            var ev = new EventDTO();
            ev.Time = DateTime.Now;
            ev.PerformerList = listPerformers;
            ev.EventTypeList = listEventTypes;
            return View(ev);
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Create(EventDTO dto)
        {
            if (!ModelState.IsValid)
            {
                dto.EventTypeList = await _eventTypeService.GetAllEventTypes();
                dto.PerformerList = await _performerService.GetAllPerformers();
                return View(dto);
            }

            var resourceManager = new ResourceManager("RWAEvent.WebApp.Resources.Views.Event.Create", typeof(LoginController).Assembly);

            dto.PerformerList = dto.SelectedPerformerIds?
                .Select(id => new PerformerDTO { Id = id })
                .ToList() ?? new List<PerformerDTO>();

            await _eventService.CreateEvent(dto);

            return RedirectToAction("List");
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Delete(int id)
        {
            bool isDeleted = await _eventService.DeleteEvent(id);
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
            var eventDto = await _eventService.GetEventById(id);

            var viewModel = new EventEditVM
            {
                Event = eventDto,
                EventTypes = await _eventTypeService.GetAllEventTypes(),
                PerformerList = await _performerService.GetAllPerformers()
            };

            return View(viewModel);
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(EventEditVM viewModel)
        {
            if (!ModelState.IsValid)
            {
                viewModel.EventTypes = await _eventTypeService.GetAllEventTypes();
                viewModel.PerformerList = await _performerService.GetAllPerformers();
                return View(viewModel);
            }

            viewModel.Event.PerformerList = viewModel.Event.SelectedPerformerIds
                .Select(id => new PerformerDTO { Id = id })
                .ToList();

            bool isUpdated = await _eventService.UpdateEvent(viewModel.Event);
            if (!isUpdated)
                return View("Error");

            return RedirectToAction("List");
        }
    }
}
