#include <stdio.h>
#include <stdint.h>
#include <atomic>
#include <thread>
#include <vector>
#include <mutex>
#include <algorithm>
#include<atomic>
#include <chrono>
#include <iostream>

/* This program illustrates the effect of the "false sharing" issue.

It launches nrThreads threads, each thread operating on a distinct 32-bit integer.

The said integers are allocated at addresses multiple of the 'allignment' parameter given on the command line. By choosing this parameter, we can make the integer to be allocated
in the same cache line or in distinct cache lines.

Having the integers in the same cache line will make the common cache line "ping-pong" between the L1 caches of the CPU cores, resulting in a much worse performance, compared to the case
when they are in distinct cache lines and no interferrence occurs between the cores.

Note: Use atomic integer or compile with no optimization (-O0) to avoid the compiler optimizing away the memory access.
*/

//#define USE_ATOMIC
unsigned const nrThreads = 8;
uint64_t const nrIterations = 0x10000000;


#ifdef USE_ATOMIC
typedef std::atomic<uint32_t> Element;
#else
typedef uint32_t Element;
#endif

/* Computes in *el (assumed to be 0 initially) the sum 0+1+2+...+(iterations-1) modulo 256; this is just in order to access *el a lot of times
*/
void add(Element* const el, uint64_t const iterations)
{
	for (uint64_t i = 0; i < iterations; ++i) {
		*el += (i & 0xFFu);
	}
}

/* Computes the expected value for the previous function, using a short formula to be quick
*/
uint32_t expected(uint64_t iterations)
{
	uint64_t const cycles = iterations / 0x100u;
	unsigned remaining = iterations % 0x100u;
	uint64_t cycleLen = 0xFFu * 0x100u / 2u;
	return uint32_t(cycleLen * cycles + (remaining * (remaining + 1u)) / 2u);
}

int main(int argc, char** argv)
{
	// Get the alignment from the command line arguments
	int alignment;
	if (argc != 2 || 1 != sscanf(argv[1], "%d", &alignment)) {
		std::cerr << "usage: false-sharing <alignment>";
		return 1;
	}
	if (alignment <= 0 || alignment % sizeof(Element) != 0) {
		std::cerr << "Alignment must me a positive multiple of " << sizeof(Element) << std::endl;
		return 1;
	}

	// Allocate nrThreads elements, each one at an address that is multiple of 'alignment'
	size_t const allocated = (nrThreads + 1) * alignment;
	size_t every = alignment / sizeof(Element);

	std::unique_ptr<char[]> buf{ new char[allocated] };
	size_t const offset = size_t(buf.get()) % alignment;
	Element* const elements = reinterpret_cast<Element*>(buf.get() +
		(offset == 0 ? 0 : alignment - offset));

	std::cout << "Allocated from " << (const void*)buf.get() << " to " <<
		(const void*)(buf.get() + allocated) << " (" << allocated  << " bytes)";
	for (unsigned i = 0; i < nrThreads; ++i) {
		std::cout << "  elements[" << i << "] at " << (void const*)(elements + i*every) << " ... "
			<< (void const*)(elements + i*every + 1) << std::endl;
	}

	std::vector<std::thread> threads;
	threads.reserve(nrThreads);
	for (unsigned i = 0; i < nrThreads; ++i) {
		elements[i * every] = 0;
	}
	std::chrono::system_clock::time_point const before = std::chrono::system_clock::now();
	for (unsigned i = 0; i < nrThreads; ++i) {
		threads.emplace_back(add, elements + i * every, nrIterations);
	}
	for (unsigned i = 0; i < nrThreads; ++i) {
		threads[i].join();
	}
	std::chrono::system_clock::time_point const after = std::chrono::system_clock::now();
	std::cout << "Duration " << std::chrono::duration_cast<std::chrono::milliseconds>(after - before).count() << "ms\n";

	uint32_t const e = expected(nrIterations);
	bool ok = true;
	for (unsigned i = 0; i < nrThreads; ++i) {
		if (e != elements[i * every]) {
			ok = false;
#ifdef USE_ATOMIC
			printf("For i=%u: expected %u, actual %u\n", i, e, elements[i * every].load());
#else
			printf("For i=%u: expected %u, actual %u\n", i, e, elements[i * every]);
#endif
		}
	}
	std::cout << (ok ? "ok" : "WRONG") << std::endl;
}