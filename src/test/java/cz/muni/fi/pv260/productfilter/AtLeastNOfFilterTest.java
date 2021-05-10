package cz.muni.fi.pv260.productfilter;

import static org.mockito.Mockito.*;
import org.junit.Test;

public class AtLeastNOfFilterTest {

    @Test(expected = FilterNeverSucceeds.class)
    public void testConstructorThrowsFilterNeverSucceeds() {
        AtLeastNOfFilter filter1 = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter2 = mock(AtLeastNOfFilter.class);
        new AtLeastNOfFilter<AtLeastNOfFilter>(3, filter1, filter2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorThrowsIllegalArgumentException() {
        AtLeastNOfFilter filter1 = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter2 = mock(AtLeastNOfFilter.class);
        new AtLeastNOfFilter<AtLeastNOfFilter>(0, filter1, filter2);
    }

    @Test
    public void testFilterPassesIfNChildFiltersPass() {
        AtLeastNOfFilter arg_filter = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter1 = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter2 = mock(AtLeastNOfFilter.class);
        when(filter1.passes(arg_filter)).thenReturn(true);
        when(filter2.passes(arg_filter)).thenReturn(true);
        AtLeastNOfFilter main_filter = new AtLeastNOfFilter(2, filter1, filter2);
        assert(main_filter.passes(arg_filter));
    }

    @Test
    public void testFilterFailsIfLeesThanNChildFiltersPass() {
        AtLeastNOfFilter arg_filter = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter1 = mock(AtLeastNOfFilter.class);
        AtLeastNOfFilter filter2 = mock(AtLeastNOfFilter.class);
        when(filter1.passes(arg_filter)).thenReturn(false);
        when(filter2.passes(arg_filter)).thenReturn(true);
        AtLeastNOfFilter main_filter = new AtLeastNOfFilter(2, filter1, filter2);
        assert(!main_filter.passes(arg_filter));
    }
}
