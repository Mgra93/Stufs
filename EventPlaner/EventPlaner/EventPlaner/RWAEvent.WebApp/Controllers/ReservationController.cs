using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RWAEvent.BL.Models.DTO.Event;
using RWAEvent.BL.Models.DTO.EventType;
using RWAEvent.BL.Models.DTO.Reservation;
using RWAEvent.BL.Models.DTO.User;
using RWAEvent.BL.Models.Filters;
using RWAEvent.BL.Services;
using RWAEvent.WebApp.Helper;
using RWAEvent.WebApp.ViewsModel;

namespace RWAEvent.WebApp.Controllers
{
    public class ReservationController : Controller
    {
        private readonly IReservation _reservationService;
        private readonly IEvent _eventService;
        private readonly IUser _userService;
        private readonly IEventType _eventTypeService;

        public ReservationController(IReservation reservationService, IEvent eventService, IUser userService, IEventType eventTypeService)
        {
            _reservationService = reservationService;
            _eventService = eventService;
            _userService = userService;
            _eventTypeService = eventTypeService;
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> List(string? search, int page = 1, int pageSize = 10)
        {
            var username = User.Identity?.Name;

            var filter = new ReservationFilter
            {
                User = username,
                Search = search,
                Page = page,
                PageSize = pageSize
            };

            var reservations = await _reservationService.GetReservationsByFilter(filter);
            var totalCount = reservations.Count;

            var model = new ReservationVM
            {
                Reservations = reservations,
                CurrentPage = page,
                TotalPages = (int)Math.Ceiling((double)totalCount / pageSize),
                Filter = filter
            };

            return View(model);
        }

        [HttpPost]
        [Authorize]
        public IActionResult List(ReservationVM viewModel)
        {
            var filter = viewModel.Filter ?? new ReservationFilter();

            return RedirectToAction("List", new
            {
                page = 1,
                pageSize = filter.PageSize,
                search = filter.Search
            });
        }

        [HttpPost]
        [Authorize]
        public async Task<IActionResult> AddToCart([FromBody] ReservationDTO item)
        {
            var cart = HttpContext.Session.GetObjectFromJson<List<ReservationDTO>>("Cart") ?? new List<ReservationDTO>();

            var existing = cart.FirstOrDefault(r => r.EventId == item.EventId);
            if (existing != null)
            {
                existing.TicketNumber += item.TicketNumber;
            }
            else
            {
                var ev = await _eventService.GetEventById((int)item.EventId);
                if (ev == null)
                    return NotFound();

                item.Event = new EventDTO
                {
                    Id = ev.Id,
                    Title = ev.Title,
                    Price = ev.Price
                };

                cart.Add(item);
            }

            HttpContext.Session.SetObjectAsJson("Cart", cart);
            return Ok(new { count = cart.Sum(x => x.TicketNumber) });
        }

        [HttpGet]
        [Authorize]
        public IActionResult GetCartCount()
        {
            var cart = HttpContext.Session.GetObjectFromJson<List<ReservationDTO>>("Cart") ?? new List<ReservationDTO>();
            var eventNumber = cart.Sum(x => x.TicketNumber);
            return Ok(new { eventNumber });
        }

        [HttpGet]
        [Authorize]
        public IActionResult GetCartPartial()
        {
            var cart = HttpContext.Session.GetObjectFromJson<List<ReservationDTO>>("Cart") ?? new List<ReservationDTO>();
            var model = new CartVM
            {
                Reservations = cart
            };
            return PartialView("Cart", model);
        }

        [HttpGet]
        [Authorize]
        public IActionResult RemoveFromCart(int eventId)
        {
            var cart = HttpContext.Session.GetObjectFromJson<List<ReservationDTO>>("Cart") ?? new List<ReservationDTO>();
            var item = cart.FirstOrDefault(x => x.EventId == eventId);

            if (item != null)
            {
                cart.Remove(item);
                HttpContext.Session.SetObjectAsJson("Cart", cart);
            }

            return Json(new { success = true, empty = !cart.Any() });
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> Buy()
        {
            var cart = HttpContext.Session.GetObjectFromJson<List<ReservationDTO>>("Cart") ?? new List<ReservationDTO>();
            var username = User.Identity?.Name;
            int savedNumber = await _reservationService.CreateReservations(cart, username);

            cart.Clear();
            HttpContext.Session.SetObjectAsJson("Cart", cart);

            return RedirectToAction("List", "Event");
        }

        [HttpGet]
        [Authorize]
        public async Task<IActionResult> Cancel(int id)
        {
            await _reservationService.CancelReservation(id);
            return RedirectToAction("List");
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Delete(int id)
        {
            bool isDeleted = await _reservationService.DeleteReservation(id);

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
            var reservation = await _reservationService.GetReservationById(id) ?? new ReservationDTO();
            int selectedEventTypeId = reservation.Event?.EventTypeId ?? 0;

            List<EventDTO> eventList = new List<EventDTO>();

            if (selectedEventTypeId != 0)
            {
                eventList = await _eventService.GetEventsByType(selectedEventTypeId) ?? new List<EventDTO>();
            }

            var userList = await _userService.GetAllUsers() ?? new List<UserDTO>();
            var eventTypes = await _eventTypeService.GetAllEventTypes() ?? new List<EventTypeDTO>();

            var reservationViewModel = new ReservationEditVM
            {
                Reservation = reservation,
                EventList = eventList,
                UserList = userList,
                EventTypeList = eventTypes,
                SelectedEventTypeId = selectedEventTypeId
            };

            return View(reservationViewModel);
        }

        [HttpPost]
        [Authorize(Roles = "Admin")]
        public async Task<IActionResult> Edit(ReservationEditVM dto)
        {
            if (!ModelState.IsValid)
            {
                dto.UserList = await _userService.GetAllUsers() ?? new List<UserDTO>();
                dto.EventTypeList = await _eventTypeService.GetAllEventTypes() ?? new List<EventTypeDTO>();
                dto.EventList = dto.SelectedEventTypeId.HasValue ? await _eventService.GetEventsByType(dto.SelectedEventTypeId.Value) ?? new List<EventDTO>() : new List<EventDTO>();

                return View(dto);
            }

            bool updated = await _reservationService.UpdateReservation(dto.Reservation);
            if (!updated)
                return View("Error");

            return RedirectToAction("List");
        }

        [HttpGet]
        public async Task<IActionResult> Details(int id)
        {
            var reservation = await _reservationService.GetReservationById(id);
            if (reservation == null)
                return NotFound();

            return View(reservation);
        }

        [HttpGet]
        public async Task<IActionResult> GetEventsByType(int eventTypeId)
        {
            var events = await _eventService.GetEventsByType(eventTypeId) ?? new List<EventDTO>();
            return Json(events.Select(e => new { e.Id, e.Title }));
        }
    }
}
